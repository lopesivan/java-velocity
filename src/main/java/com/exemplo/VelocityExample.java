package com.exemplo;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Exemplo melhorado de Velocity com sugar syntax e boas práticas
 */
public class VelocityExample {
    
    // Singleton lazy-loaded do VelocityEngine
    private static class VelocityEngineHolder {
        private static final VelocityEngine INSTANCE = createEngine();
        
        private static VelocityEngine createEngine() {
            var props = new Properties();
            props.setProperty(RuntimeConstants.RESOURCE_LOADERS, "class");
            props.setProperty("resource.loader.class.class", 
                            ClasspathResourceLoader.class.getName());
            
            var engine = new VelocityEngine(props);
            engine.init();
            return engine;
        }
    }
    
    /**
     * Renderiza um template com contexto simplificado
     */
    public static String render(String templatePath, Map<String, Object> context) {
        var engine = VelocityEngineHolder.INSTANCE;
        var template = engine.getTemplate(templatePath, "UTF-8");
        var ctx = new VelocityContext(context);
        
        try (var writer = new StringWriter()) {
            template.merge(ctx, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao renderizar template: " + templatePath, e);
        }
    }
    
    /**
     * Builder fluente para criar contextos Velocity
     */
    public static class ContextBuilder {
        private final VelocityContext context = new VelocityContext();
        
        public ContextBuilder put(String key, Object value) {
            context.put(key, value);
            return this;
        }
        
        public ContextBuilder putAll(Map<String, Object> values) {
            values.forEach(context::put);
            return this;
        }
        
        public VelocityContext build() {
            return context;
        }
        
        public String render(String templatePath) {
            var engine = VelocityEngineHolder.INSTANCE;
            var template = engine.getTemplate(templatePath, "UTF-8");
            
            try (var writer = new StringWriter()) {
                template.merge(context, writer);
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException("Erro ao renderizar: " + templatePath, e);
            }
        }
    }
    
    // ========== EXEMPLOS DE USO ==========
    
    public static void main(String[] args) {
        exemplo1_Tradicional();
        exemplo2_ComMap();
        exemplo3_ComBuilder();
        exemplo4_Inline();
    }
    
    /**
     * Exemplo 1: Forma tradicional melhorada
     */
    private static void exemplo1_Tradicional() {
        System.out.println("=== Exemplo 1: Tradicional ===");
        
        var engine = VelocityEngineHolder.INSTANCE;
        var template = engine.getTemplate("template.vm", "UTF-8");
        
        var ctx = new VelocityContext();
        ctx.put("nome", "Ivan");
        ctx.put("mensagem", "Projeto Velocity funcionando!");
        
        try (var writer = new StringWriter()) {
            template.merge(ctx, writer);
            System.out.println(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Exemplo 2: Usando Map (mais conciso)
     */
    private static void exemplo2_ComMap() {
        System.out.println("\n=== Exemplo 2: Com Map ===");
        
        var resultado = render("template.vm", Map.of(
            "nome", "Ivan",
            "mensagem", "Usando Map.of() do Java 9+",
            "versao", "2.0"
        ));
        
        System.out.println(resultado);
    }
    
    /**
     * Exemplo 3: Usando Builder fluente
     */
    private static void exemplo3_ComBuilder() {
        System.out.println("\n=== Exemplo 3: Com Builder ===");
        
        var resultado = new ContextBuilder()
            .put("nome", "Ivan")
            .put("mensagem", "Builder pattern para construção fluente!")
            .put("itens", java.util.List.of("Item 1", "Item 2", "Item 3"))
            .render("template.vm");
        
        System.out.println(resultado);
    }
    
    /**
     * Exemplo 4: Template inline (sem arquivo)
     */
    private static void exemplo4_Inline() {
        System.out.println("\n=== Exemplo 4: Template Inline ===");
        
        var engine = VelocityEngineHolder.INSTANCE;
        var templateString = "Olá, $nome! Hoje é $data.";
        
        var ctx = new VelocityContext(Map.of(
            "nome", "Ivan",
            "data", java.time.LocalDate.now()
        ));
        
        try (var writer = new StringWriter()) {
            engine.evaluate(ctx, writer, "inline-template", templateString);
            System.out.println(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// ========== CLASSE UTILITÁRIA OPCIONAL ==========

/**
 * Utilitário estático para uso rápido do Velocity
 */
class Velocity {
    
    private static final VelocityEngine ENGINE = initEngine();
    
    private static VelocityEngine initEngine() {
        var props = new Properties();
        props.setProperty(RuntimeConstants.RESOURCE_LOADERS, "class");
        props.setProperty("resource.loader.class.class", 
                        ClasspathResourceLoader.class.getName());
        
        var engine = new VelocityEngine(props);
        engine.init();
        return engine;
    }
    
    /**
     * Renderiza template com varargs
     * 
     * Uso: Velocity.render("template.vm", "nome", "Ivan", "idade", 30)
     */
    public static String render(String template, Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Número de argumentos deve ser par");
        }
        
        var ctx = new VelocityContext();
        for (int i = 0; i < keyValues.length; i += 2) {
            ctx.put(keyValues[i].toString(), keyValues[i + 1]);
        }
        
        try (var writer = new StringWriter()) {
            ENGINE.getTemplate(template, "UTF-8").merge(ctx, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao renderizar", e);
        }
    }
    
    /**
     * Renderiza template inline
     */
    public static String eval(String templateString, Map<String, Object> context) {
        var ctx = new VelocityContext(context);
        
        try (var writer = new StringWriter()) {
            ENGINE.evaluate(ctx, writer, "eval", templateString);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro na avaliação", e);
        }
    }
    
    /**
     * Cria um builder para contexto
     */
    public static ContextBuilder context() {
        return new ContextBuilder();
    }
    
    public static class ContextBuilder {
        private final VelocityContext ctx = new VelocityContext();
        
        public ContextBuilder add(String key, Object value) {
            ctx.put(key, value);
            return this;
        }
        
        public String render(String template) {
            try (var writer = new StringWriter()) {
                ENGINE.getTemplate(template, "UTF-8").merge(ctx, writer);
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public String eval(String templateString) {
            try (var writer = new StringWriter()) {
                ENGINE.evaluate(ctx, writer, "eval", templateString);
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

// ========== EXEMPLOS DE USO DA CLASSE UTILITÁRIA ==========

class ExemplosRapidos {
    public static void main(String[] args) {
        
        // Exemplo super conciso com varargs
        System.out.println(
            Velocity.render("template.vm", 
                "nome", "Ivan",
                "mensagem", "Super conciso!"
            )
        );
        
        // Template inline
        System.out.println(
            Velocity.eval("Olá, $nome!", Map.of("nome", "Ivan"))
        );
        
        // Builder fluente
        System.out.println(
            Velocity.context()
                .add("nome", "Ivan")
                .add("idade", 30)
                .eval("$nome tem $idade anos")
        );
    }
}
