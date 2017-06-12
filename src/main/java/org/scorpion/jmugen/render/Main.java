package org.scorpion.jmugen.render;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.scorpion.jmugen.core.config.*;
import org.scorpion.jmugen.core.element.GameObject;
import org.scorpion.jmugen.core.element.Stage;
import org.scorpion.jmugen.core.maths.Matrix4f;
import org.scorpion.jmugen.core.render.FpsCounter;
import org.scorpion.jmugen.core.render.Renderable;
import org.scorpion.jmugen.exception.InitializationError;
import org.scorpion.jmugen.exception.NonFatal;
import org.scorpion.jmugen.io.input.keyboard.KeyboardInputHandler;
import org.scorpion.jmugen.util.FileResource;
import org.scorpion.jmugen.util.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main implements Runnable {

    ExecutorService diskService;

    private boolean shouldTerminate;
    private boolean silhouetteMode;
    private long window;
    private SystemConfig systemConfig;

    KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
    FpsCounter fpsCounter = new FpsCounter();

    List<GameObject<? extends Def>> elements = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        shouldTerminate = false;
        Thread thread = new Thread(this, "Main");
        thread.start();
    }

    @Override
    public void run() {
        init();
        long ts = System.currentTimeMillis();
        try {
            while (!shouldTerminate) {
                update();
                render();
                fpsCounter.incrementFrame();
                long now = System.currentTimeMillis();
                if (now - ts > 1e3) {
                    glfwSetWindowTitle(window, "JMugen " + fpsCounter.getFps());
                    ts = now;
                }
                if (glfwWindowShouldClose(window)) {
                    shouldTerminate = true;
                }
            }
            glfwDestroyWindow(window);
            glfwTerminate();
        } finally {
            cleanUp();
        }
    }

    private void bootstrap() {
        diskService = Executors.newSingleThreadExecutor();
        StageDef stageDef = new StageDef("kfm", DefParser.parse(new FileResource("data/stages/kfm.def"))).load();
        Stage stage = new Stage(systemConfig, stageDef, keyboardInputHandler);
        elements.add(stage);
    }

    private void loadGlobalConfig() {
        FileResource configFile = new FileResource("config.properties");
        LOG.info("Loading configs from " + configFile);
        Map<String, String> configs;
        try {
            configs = PropertyUtils.loadConfig(configFile);
        } catch (IOException e) {
            throw new InitializationError("Failed to load configs", e);
        }
        systemConfig = new SystemConfig(configs);
        LOG.info("Configs successfully loaded");
    }

    private void loadResources() {
        LOG.info("Loading resources from " + systemConfig.getResourceHome());
        List<Callable<Void>> loadables = elements.stream().map(g -> (Callable<Void>) g::load).collect(Collectors.toList());
        try {
            for (Future<Void> f : diskService.invokeAll(loadables)) {
                f.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        loadGlobalConfig();
        bootstrap();
        loadResources();

        if (!glfwInit()) {
            throw new RuntimeException();
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        window = glfwCreateWindow(systemConfig.getWindowWidth(), systemConfig.getWindowHeight(), "JMugen", NULL, NULL);

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (videoMode.width() - systemConfig.getWindowWidth()) / 2,
                (videoMode.height() - systemConfig.getWindowHeight()) / 2);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwSetKeyCallback(window, keyboardInputHandler);
        keyboardInputHandler.registerStrokeHandler(GLFW.GLFW_KEY_F2, (mods) -> {
            this.silhouetteMode = !this.silhouetteMode;
        });

        glfwShowWindow(window);
        fpsCounter.init();

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        Shaders.loadBackgroundShader();

        Shader bgShader = Shaders.getBackgroundShader();
        bgShader.enable();
        Matrix4f projectionMatrix = Matrix4f.orthographic(
                0,
                systemConfig.getGameWidth(),
                0,
                -systemConfig.getGameHeight(),
                -1.0f,
                1.0f
        );

        bgShader.setMatrix4f("proj_mat", projectionMatrix);
        bgShader.setUniform1i("tex", 1);
        bgShader.setUniform1i("silhouette_mode", 0);
        bgShader.disable();

        for (Renderable renderable : elements) {
            renderable.init();
        }
    }

    private void update() {
        glfwPollEvents();
        if (keyboardInputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
            shouldTerminate = true;
        }
        for (Renderable renderable : elements) {
            renderable.update();
        }
        Shader bg = Shaders.getBackgroundShader();
        bg.enable();
        bg.setUniform1i("silhouette_mode", this.silhouetteMode ? 1 : 0);
        bg.disable();
    }

    private void render() {
        try {
            glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            for (Renderable renderable : elements) {
                renderable.render();
            }
            int errCode = glGetError();
            if (errCode != GL_NO_ERROR) {
                LOG.warn("OpenGL error: " + errCode);
            }
            glfwSwapBuffers(window);
        } catch (Throwable e) {
            if (e instanceof NonFatal) {
                LOG.error(e.getMessage(), e);
                return;
            }
            throw e;
        }
    }

    private void cleanUp() {
        if (diskService != null) {
            diskService.shutdownNow();
        }
    }
}
