package dev.vansen.pancakecore.loader;

import dev.vansen.pancakecore.loader.depender.Depender;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class PancakeLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        String[][] dependencies = {
                {"org.apache.fury", "fury-core", "0.9.0"}
        };

        classpathBuilder.getContext().getLogger().info("Downloading/Loading all dependencies...");
        long start = System.currentTimeMillis();
        Depender.create()
                .repo("https://repo1.maven.org/maven2")
                .destination(classpathBuilder.getContext().getDataDirectory() + "/libs/")
                .classpathBuilder(classpathBuilder)
                .exclude("srczip")
                .download(ObjectArrayList.of(dependencies));
        Depender.create()
                .repo("https://jitpack.io")
                .destination(classpathBuilder.getContext().getDataDirectory() + "/libs/")
                .classpathBuilder(classpathBuilder)
                .downloadOnlyOne("com.github.vansencool", "NoksDB", "1.0.5");
        classpathBuilder.getContext()
                .getLogger()
                .info("Finished downloading/loading all dependencies in {}ms", System.currentTimeMillis() - start);
    }
}