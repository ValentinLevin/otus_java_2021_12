package com.example;

import com.example.appcontainer.AppComponentsContainerImpl;
import com.example.config.AppConfig;
import com.example.services.EquationPreparer;
import com.example.services.IOService;
import com.example.services.PlayerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    @DisplayName("Из контекста тремя способами должен корректно доставаться компонент с проставленными полями")
    @ParameterizedTest(name = "Достаем по: {0}")
    @CsvSource(value = {"GameProcessor, com.example.services.GameProcessor",
            "GameProcessorImpl, com.example.services.GameProcessor",
            "gameProcessor, com.example.services.GameProcessor",

            "IOService, com.example.services.IOService",
            "IOServiceStreams, com.example.services.IOService",
            "ioService, com.example.services.IOService",

            "PlayerService, com.example.services.PlayerService",
            "PlayerServiceImpl, com.example.services.PlayerService",
            "playerService, com.example.services.PlayerService",

            "EquationPreparer, com.example.services.EquationPreparer",
            "EquationPreparerImpl, com.example.services.EquationPreparer",
            "equationPreparer, com.example.services.EquationPreparer"
    })
    public void shouldExtractFromContextCorrectComponentWithNotNullFields(String classNameOrBeanId, Class<?> rootClass) throws Exception {
        var ctx = new AppComponentsContainerImpl(AppConfig.class);

        assertThat(classNameOrBeanId).isNotEmpty();
        Object component;
        if (classNameOrBeanId.charAt(0) == classNameOrBeanId.toUpperCase().charAt(0)) {
            Class<?> gameProcessorClass = Class.forName("com.example.services." + classNameOrBeanId);
            assertThat(rootClass).isAssignableFrom(gameProcessorClass);

            component = ctx.getAppComponent(gameProcessorClass);
        } else {
            component = ctx.getAppComponent(classNameOrBeanId);
        }
        assertThat(component).isNotNull();
        assertThat(rootClass).isAssignableFrom(component.getClass());

        var fields = Arrays.stream(component.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());

        for (var field: fields){
            var fieldValue = field.get(component);
            assertThat(fieldValue).isNotNull().isInstanceOfAny(IOService.class, PlayerService.class,
                    EquationPreparer.class, PrintStream.class, Scanner.class);
        }

    }
}