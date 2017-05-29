package org.scorpion.jmugen.render;

import org.scorpion.jmugen.core.maths.Matrix4f;
import org.scorpion.jmugen.core.maths.Vector3f;
import org.scorpion.jmugen.render.util.ShaderUtils;
import org.scorpion.jmugen.util.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private static final Logger LOG = LoggerFactory.getLogger(Shader.class);

    private String name;
    private int programId;
    private Resource vertex;
    private Resource fragment;
    private Map<String, Integer> locationCache = new HashMap<>();

    public Shader(String name, Resource vertex, Resource fragment) {
        this.name = name;
        this.vertex = vertex;
        this.fragment = fragment;
    }

    public void load() {
        programId = ShaderUtils.loadShaders(vertex, fragment);
        LOG.debug("Shader " + this.name + " has been loaded to " + programId);
    }

    public void setUniform1i(String name, int value) {
        int loc = getUniformLocation(name);
        if (loc == -1) {
            return;
        }
        glUniform1i(loc, value);
    }

    public void setUniform2f(String name, float x, float y) {
        int loc = getUniformLocation(name);
        if (loc == -1) {
            return;
        }
        glUniform2f(loc, x, y);
    }

    public void setUniform3f(String name, Vector3f vector) {
        int loc = getUniformLocation(name);
        if (loc == -1) {
            return;
        }
        glUniform3f(loc, vector.x, vector.y, vector.z);
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
        int loc = getUniformLocation(name);
        if (loc == -1) {
            return;
        }
        glUniformMatrix4fv(loc, false, matrix.toFloatBuffer());
    }

    public int getUniformLocation(String varName) {
        Integer loc = locationCache.get(varName);
        if (loc != null) {
            return loc;
        }

        Integer location = glGetUniformLocation(programId, varName);
        if (location == -1) {
            System.err.println("no uniform for " + varName + " in shader " + this.name);
        }
        else {
            locationCache.put(varName, location);
        }
        return location;
    }

    public void enable() {
        glUseProgram(programId);
    }

    public void disable() {
        glUseProgram(programId);
    }

}
