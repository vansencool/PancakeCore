package dev.vansen.pancakecore.loader.depender;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A utility class for downloading dependencies with version resolution.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
@ThreadSafe
public class Depender {

    private String repoUrl;
    private String destinationDir;
    private PluginClasspathBuilder classpathBuilder;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Set<String> resolvedDependencies = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<String, String> latestVersions = new ConcurrentHashMap<>();
    private final List<String> excluded = new ObjectArrayList<>();

    /**
     * Creates a new instance of the Depender class.
     */
    public static Depender create() {
        return new Depender();
    }

    /**
     * Sets the repository URL.
     *
     * @param repoUrl the repository URL
     * @return this instance for chaining
     */
    public Depender repo(@NotNull String repoUrl) {
        this.repoUrl = repoUrl;
        return this;
    }

    /**
     * Sets the destination directory.
     *
     * @param destinationDir the destination directory
     * @return this instance for chaining
     */
    public Depender destination(@NotNull String destinationDir) {
        this.destinationDir = destinationDir;
        return this;
    }

    /**
     * Sets the classpath builder.
     *
     * @param classpathBuilder the classpath builder
     * @return this instance for chaining
     */
    public Depender classpathBuilder(@NotNull PluginClasspathBuilder classpathBuilder) {
        this.classpathBuilder = classpathBuilder;
        return this;
    }

    /**
     * Excludes the specified dependencies.
     *
     * @param excluded the dependencies to exclude
     * @return this instance for chaining
     */
    public Depender exclude(@NotNull String... excluded) {
        this.excluded.addAll(ObjectArrayList.of(excluded));
        return this;
    }

    /**
     * Downloads all dependencies from the repository.
     *
     * @param dependencies the dependencies to download
     */
    public void download(@NotNull List<String[]> dependencies) {
        dependencies.parallelStream()
                .collect(Collectors.groupingBy(dep -> dep[0] + ":" + dep[1]))
                .values()
                .parallelStream()
                .forEach(this::resolveVersion);
    }

    /**
     * Downloads a specific dependency from the repository.
     *
     * @param group    the group of the dependency
     * @param artifact the artifact of the dependency
     * @param version  the version of the dependency
     */
    public void download(@NotNull String group, @NotNull String artifact, @NotNull String version) {
        resolve(group, artifact, version);
    }

    private void resolveVersion(@NotNull List<String[]> versions) {
        String[] latest = versions.parallelStream()
                .max((a, b) -> compareVersions(a[2], b[2]))
                .orElseThrow();

        resolve(latest[0], latest[1], latest[2]);
    }

    private void resolve(@NotNull String group, @NotNull String artifact, @NotNull String version) {
        if (!latestVersions.compute(group + ":" + artifact, (k, v) -> v == null || compareVersions(version, v) > 0 ? version : v).equals(version)) {
            return;
        }

        if (!resolvedDependencies.add(group + ":" + artifact + ":" + version)) return;

        downloadJar(group, artifact, version);

        try {
            parse(fetchOrCache(Paths.get(destinationDir, "poms", group.replace(".", "/"), artifact, version, artifact + "-" + version + ".pom"), group, artifact, version))
                    .parallelStream()
                    .forEach(dep -> resolve(dep[0], dep[1], dep[2]));
        } catch (Exception e) {
            classpathBuilder.getContext()
                    .getLogger()
                    .error("Failed to resolve dependencies for {} v{}", artifact, version, e);
        }
    }

    private void downloadJar(@NotNull String group, @NotNull String artifact, @NotNull String version) {
        Path jarPath = Paths.get(destinationDir, artifact + "-" + version + ".jar");

        if (Files.exists(jarPath)) {
            classpathBuilder.addLibrary(new JarLibrary(jarPath));
            return;
        }

        try {
            Files.createDirectories(jarPath.getParent());
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(String.format("%s/%s/%s/%s/%s-%s.jar",
                    repoUrl, group.replace(".", "/"), artifact, version, artifact, version))).build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofFile(jarPath));
            classpathBuilder.addLibrary(new JarLibrary(jarPath));
        } catch (Exception e) {
            classpathBuilder.getContext()
                    .getLogger()
                    .error("Failed to download JAR: {} v{}", artifact, version, e);
        }
    }

    private String fetchOrCache(@NotNull Path pomPath, @NotNull String group, @NotNull String artifact, @NotNull String version) throws IOException, InterruptedException {
        if (Files.exists(pomPath)) {
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(pomPath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                StringBuilder content = new StringBuilder();
                while ((bytesRead = bis.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead));
                }
                return content.toString();
            }
        }

        String pomContent = httpClient.send(HttpRequest.newBuilder().uri(URI.create(String.format("%s/%s/%s/%s/%s-%s.pom",
                        repoUrl, group.replace(".", "/"), artifact, version, artifact, version)))
                .build(), HttpResponse.BodyHandlers.ofString()).body();
        Files.createDirectories(pomPath.getParent());
        Files.writeString(pomPath, pomContent);
        return pomContent;
    }

    private List<String[]> parse(@NotNull String pomContent) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(pomContent)));
        doc.getDocumentElement().normalize();

        NodeList dependencies = doc.getElementsByTagName("dependency");
        List<String[]> results = new ObjectArrayList<>();

        ConcurrentMap<String, String> properties = new ConcurrentHashMap<>();
        NodeList propertiesList = doc.getElementsByTagName("properties");
        if (propertiesList.getLength() > 0) {
            NodeList propertyNodes = ((Element) propertiesList.item(0)).getElementsByTagName("*");
            for (int i = 0; i < propertyNodes.getLength(); i++) {
                Element property = (Element) propertyNodes.item(i);
                properties.put(property.getNodeName(), property.getTextContent());
            }
        }

        NodeList parentList = doc.getElementsByTagName("parent");
        if (parentList.getLength() > 0) {
            NodeList versionNodes = ((Element) parentList.item(0)).getElementsByTagName("version");
            if (versionNodes.getLength() > 0) {
                properties.put("project.version", versionNodes.item(0).getTextContent());
            }
        }

        for (int i = 0; i < dependencies.getLength(); i++) {
            Element dependency = (Element) dependencies.item(i);
            String artifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();

            if (excluded.contains(artifactId)) {
                continue;
            }

            String scope = "";
            NodeList scopeNodes = dependency.getElementsByTagName("scope");
            if (scopeNodes.getLength() > 0) {
                scope = scopeNodes.item(0).getTextContent();
            }

            if ("test".equalsIgnoreCase(scope) || "provided".equalsIgnoreCase(scope)) {
                continue;
            }

            NodeList versionNodes = dependency.getElementsByTagName("version");
            if (versionNodes.getLength() == 0) {
                continue;
            }

            results.add(new String[]{
                    dependency.getElementsByTagName("groupId").item(0).getTextContent(),
                    artifactId,
                    version(versionNodes.item(0).getTextContent(), properties)
            });
        }

        return results;
    }

    private int compareVersions(@NotNull String v1, @NotNull String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        try {
            return IntStream.range(0, Math.max(parts1.length, parts2.length))
                    .map(i -> Integer.compare(
                            i < parts1.length ? Integer.parseInt(parts1[i]) : 0,
                            i < parts2.length ? Integer.parseInt(parts2[i]) : 0
                    ))
                    .filter(x -> x != 0)
                    .findFirst()
                    .orElse(0);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String version(@NotNull String version, @NotNull Map<String, String> properties) {
        if (version.startsWith("${") && version.endsWith("}")) {
            String propertyName = version.substring(2, version.length() - 1);
            if (properties.containsKey(propertyName)) {
                return properties.get(propertyName);
            } else {
                classpathBuilder.getContext()
                        .getLogger()
                        .error("Property {} not found.", propertyName);
            }
        }
        return version;
    }
}