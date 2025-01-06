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
                {"com.esotericsoftware", "kryo", "5.6.2"},
                {"com.github.ben-manes.caffeine", "caffeine", "3.1.8"},
        };

        classpathBuilder.getContext().getLogger().info("Downloading/Loading all dependencies...");
        long start = System.currentTimeMillis();
        Depender.create()
                .repo("https://repo1.maven.org/maven2")
                .destination(classpathBuilder.getContext().getDataDirectory() + "/libs/")
                .classpathBuilder(classpathBuilder)
                .download(ObjectArrayList.of(dependencies));
        classpathBuilder.getContext()
                .getLogger()
                .info("Finished downloading/loading all dependencies in {}ms", System.currentTimeMillis() - start);
    }
}