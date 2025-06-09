package com.tom.createores.kubejs;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriConsumer;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public class KubeJSUtil {

	public static <T extends KubeRecipe> RecipeSchemaFunction wrapFunc(Consumer<T> function) {
		return (ctx, recipe, args) -> function.accept((T) recipe);
	}

	public static <T extends KubeRecipe, A1> RecipeSchemaFunction wrapFunc(Class<A1> param1, BiConsumer<T, A1> function) {
		TypeInfo[] params = new TypeInfo[] {TypeInfo.of(param1)};
		return new RecipeSchemaFunction() {

			@Override
			public TypeInfo[] getArgTypes() {
				return params;
			}

			@Override
			public void execute(Context cx, KubeRecipe recipe, Object[] args) {
				function.accept((T) recipe, (A1) args[0]);
			}
		};
	}

	public static <T extends KubeRecipe, A1, A2> RecipeSchemaFunction wrapFunc(Class<A1> param1, Class<A2> param2, TriConsumer<T, A1, A2> function) {
		TypeInfo[] params = new TypeInfo[] {TypeInfo.of(param1), TypeInfo.of(param2)};
		return new RecipeSchemaFunction() {

			@Override
			public TypeInfo[] getArgTypes() {
				return params;
			}

			@Override
			public void execute(Context cx, KubeRecipe recipe, Object[] args) {
				function.accept((T) recipe, (A1) args[0], (A2) args[1]);
			}
		};
	}

	public static <T extends KubeRecipe, A1, A2, A3> RecipeSchemaFunction wrapFunc(Class<A1> param1, Class<A2> param2, Class<A3> param3, QuadConsumer<T, A1, A2, A3> function) {
		TypeInfo[] params = new TypeInfo[] {TypeInfo.of(param1), TypeInfo.of(param2), TypeInfo.of(param3)};
		return new RecipeSchemaFunction() {

			@Override
			public TypeInfo[] getArgTypes() {
				return params;
			}

			@Override
			public void execute(Context cx, KubeRecipe recipe, Object[] args) {
				function.accept((T) recipe, (A1) args[0], (A2) args[1], (A3) args[2]);
			}
		};
	}

	interface QuadConsumer<I, A1, A2, A3> {
		void accept(final I inst, final A1 p1, final A2 p2, final A3 p3);
	}
}
