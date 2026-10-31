package com.finderfeed.fdbosses.client;

import com.finderfeed.fdbosses.FDBosses;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class GeneratedPerlinNoise3D {
    private static final int SIZE = 512;
    private static final int SEED = 1761130;

    private static int textureId = 0;
    private static boolean unsupported = false;

    public static int getTextureId() {
        return textureId;
    }

    public static void ensureCreated() {

        if (textureId != 0 || unsupported) return;

        if (!GL.getCapabilities().OpenGL43) {
            unsupported = true;
            FDBosses.LOGGER.error("Compute shaders require OpenGL 4.3");
            return;
        }

        int oldProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int oldActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        int oldTexture = GL11.glGetInteger(GL12.GL_TEXTURE_BINDING_3D);

        int oldImage = GL30.glGetIntegeri(GL42.GL_IMAGE_BINDING_NAME, 0);
        int oldImageLevel = GL30.glGetIntegeri(GL42.GL_IMAGE_BINDING_LEVEL, 0);
        boolean oldImageLayered = GL30.glGetIntegeri(GL42.GL_IMAGE_BINDING_LAYERED, 0) != 0;
        int oldImageLayer = GL30.glGetIntegeri(GL42.GL_IMAGE_BINDING_LAYER, 0);
        int oldImageAccess = GL30.glGetIntegeri(GL42.GL_IMAGE_BINDING_ACCESS, 0);
        int oldImageFormat = GL30.glGetIntegeri(GL42.GL_IMAGE_BINDING_FORMAT, 0);

        int texture = GL11.glGenTextures();
        int program = 0;

        try {
            GL11.glBindTexture(GL12.GL_TEXTURE_3D, texture);

            GL42.glTexStorage3D(GL12.GL_TEXTURE_3D, 1, GL30.GL_R8, SIZE, SIZE, SIZE);

            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL11.GL_REPEAT);

            ResourceLocation location = FDBosses.location("shaders/compute/perlin_noise.comp");

            String source;

            try (InputStream stream = Minecraft.getInstance().getResourceManager().open(location)) {
                source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }catch (Exception e){
                throw new IOException(e);
            }

            program = compileComputeProgram(source);

            GL20.glUseProgram(program);

            GL20.glUniform1i(GL20.glGetUniformLocation(program, "size"), SIZE);
            GL30.glUniform1ui(GL20.glGetUniformLocation(program, "seed"), SEED);
            GL30.glUniform1f(GL20.glGetUniformLocation(program, "octaves"), 6);
            GL30.glUniform1f(GL20.glGetUniformLocation(program, "sections"), 10);

            GL42.glBindImageTexture(0, texture, 0, true, 0, GL15.GL_WRITE_ONLY, GL30.GL_R8);

            int groups = (SIZE + 7) / 8;

            GL43.glDispatchCompute(groups, groups, groups);

            FDBosses.LOGGER.info("Generating 3D noise texture...");
            GL42.glMemoryBarrier(GL42.GL_TEXTURE_FETCH_BARRIER_BIT);


            FDBosses.LOGGER.info("3D noise texture generated.");
            textureId = texture;

        } catch (IOException e) {
            GL11.glDeleteTextures(texture);
            throw new UncheckedIOException(e);

        } catch (RuntimeException e) {
            GL11.glDeleteTextures(texture);
            throw e;

        } finally {
            GL20.glUseProgram(oldProgram);

            GL42.glBindImageTexture(0, oldImage, oldImageLevel, oldImageLayered, oldImageLayer, oldImageAccess, oldImageFormat);

            GL11.glBindTexture(GL12.GL_TEXTURE_3D, oldTexture);
            GL13.glActiveTexture(oldActiveTexture);

            if (program != 0) {
                GL20.glDeleteProgram(program);
            }
        }
    }

    private static int compileComputeProgram(String source) {
        int shader = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);

        try {
            GL20.glShaderSource(shader, source);
            GL20.glCompileShader(shader);

            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new IllegalStateException(GL20.glGetShaderInfoLog(shader));
            }

            int program = GL20.glCreateProgram();

            GL20.glAttachShader(program, shader);
            GL20.glLinkProgram(program);
            GL20.glDetachShader(program, shader);

            if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                String log = GL20.glGetProgramInfoLog(program);
                GL20.glDeleteProgram(program);
                throw new IllegalStateException(log);
            }

            return program;

        } finally {
            GL20.glDeleteShader(shader);
        }
    }

    public static void dispose() {
        RenderSystem.assertOnRenderThread();

        if (textureId != 0) {
            GL11.glDeleteTextures(textureId);
            textureId = 0;
        }
    }
}
