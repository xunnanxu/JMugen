package org.scorpion.jmugen.render.util;

import org.apache.commons.io.IOUtils;
import org.scorpion.jmugen.exception.InitializationError;
import org.scorpion.jmugen.util.Resource;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;


import java.io.IOException;

public class ShaderUtils {

    public static int loadShaders(Resource vertex, Resource fragment) {
        int p = glCreateProgram();
        int vertexShaderId = loadAndAttachShader(p, GL_VERTEX_SHADER, vertex);
        int fragmentShaderId = loadAndAttachShader(p, GL_FRAGMENT_SHADER, fragment);
        glLinkProgram(p);
        if (glGetProgrami(p, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(p, glGetProgrami(p, GL_INFO_LOG_LENGTH));
            throw new InitializationError("Failed to link program " + vertex + " " + fragment, log);
        }
        glValidateProgram(p);

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        return p;
    }

    private static int loadAndAttachShader(int program, int type, Resource shaderRes) throws InitializationError {
        String code;
        try {
            code = IOUtils.toString(shaderRes.load());
        } catch (IOException e) {
            throw new InitializationError("Failed to load shader " + shaderRes.getName() + ": " + e.getMessage(), e);
        }
        int id = glCreateShader(type);
        glShaderSource(id, code);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(id,
                    glGetShaderi(id, GL_INFO_LOG_LENGTH));

            throw new InitializationError("Failed to compile shader " + shaderRes.getName(), log);
        }

        glAttachShader(program, id);

        return id;
    }

}
