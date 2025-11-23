import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class VelocityExample {

    public static void main(String[] args) {
        // 1. Inicializar o Velocity Engine
        VelocityEngine ve = new VelocityEngine();
        
        // Configura para carregar templates do diretório 'src/main/resources' 
        // ou de onde o template.vm estiver (depende da sua estrutura de projeto)
        ve.setProperty(VelocityEngine.RESOURCE_LOADER_CLASS, "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        
        // 2. Criar o Contexto e adicionar dados (o Modelo)
        VelocityContext context = new VelocityContext();
        context.put("nome", "Usuário de Teste");
        
        List<String> listaDeItens = Arrays.asList("Maçã", "Banana", "Laranja");
        context.put("itens", listaDeItens);

        // 3. Carregar o Template
        // O nome do arquivo depende de onde o Velocity Engine está configurado para procurar
        Template template = ve.getTemplate("template.vm");

        // 4. Combinar o Template com o Contexto (Merge)
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        // Imprimir a saída gerada
        System.out.println("--- Saída Gerada ---\n");
        System.out.println(writer.toString());
        System.out.println("\n--------------------");
    }
}
