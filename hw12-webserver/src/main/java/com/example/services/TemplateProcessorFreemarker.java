package com.example.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class TemplateProcessorFreemarker implements TemplateProcessor {
    private static final Logger log = LoggerFactory.getLogger(TemplateProcessor.class);
    private final Configuration configuration;

    public TemplateProcessorFreemarker(String templateDir) {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(this.getClass(), templateDir);
        configuration.setDefaultEncoding("UTF-8");
    }

    @Override
    public String getPage(String filename, Map<String, Object> data) throws IOException {
        try (Writer stream = new StringWriter()) {
            Template template = configuration.getTemplate(filename);
            template.process(data, stream);
            return stream.toString();
        } catch (TemplateException e) {
            log.error("Ошибка при обработке шаблона {} при заполнении данными {}", filename, data, e);
            throw new IOException(e);
        }
    }
}
