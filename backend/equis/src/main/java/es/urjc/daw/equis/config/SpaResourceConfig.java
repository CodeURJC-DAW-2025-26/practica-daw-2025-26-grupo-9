package es.urjc.daw.equis.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class SpaResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/new/**")
            .addResourceLocations("classpath:/static/new/")
            .resourceChain(true)
            .addResolver(new SPAFallbackResolver());
    }

    private static class SPAFallbackResolver implements ResourceResolver {

        @Override
        public Resource resolveResource(HttpServletRequest request, String requestPath,
                java.util.List<? extends Resource> locations, ResourceResolverChain chain) {
            if (!requestPath.isEmpty() && !".".equals(requestPath)) {
                Resource resolved = chain.resolveResource(request, requestPath, locations);
                if (resolved != null && resolved.exists() && resolved.isReadable()) {
                    return resolved;
                }
            }
            for (Resource location : locations) {
                try {
                    Resource index = location.createRelative("index.html");
                    if (index.exists() && index.isReadable()) {
                        return index;
                    }
                } catch (IOException e) {
                    // fall through
                }
            }
            return null;
        }

        @Override
        public String resolveUrlPath(String resourcePath, java.util.List<? extends Resource> locations,
                ResourceResolverChain chain) {
            return chain.resolveUrlPath(resourcePath, locations);
        }
    }
}
