package com.exemplo;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

public class VelocityExample {
    public static void main(String[] args) {
        VelocityEngine ve = new VelocityEngine();

        // NOVO FORMATO (Velocity >= 2.3)
        // Agora a key correta é "resource.loaders"
        ve.setProperty("resource.loaders", "class");

        // E cada loader vira "resource.loader.<name>.class"
        ve.setProperty(
			"resource.loader.class.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        ve.init();

        Template template = ve.getTemplate("template.vm", "UTF-8");

        VelocityContext ctx = new VelocityContext();
        ctx.put("nome", "Ivan");
        ctx.put("mensagem", "Projeto Velocity com Maven funcionando!");

        StringWriter writer = new StringWriter();
        template.merge(ctx, writer);

        System.out.println(writer);
    }
}

