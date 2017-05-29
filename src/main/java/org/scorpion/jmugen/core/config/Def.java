package org.scorpion.jmugen.core.config;

import org.scorpion.jmugen.util.Loadable;

import java.util.function.Function;

public interface Def<T> extends Loadable<T> {

    Function<String, Boolean> BOOLEAN_CONVERTER = input -> Integer.parseInt(input) != 0;

}
