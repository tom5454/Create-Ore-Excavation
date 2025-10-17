package com.tom.createores.kubejs;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.TriConsumer;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.function.ResolvedRecipeSchemaFunction;

public class KubeJSUtil {

	public static <T extends KubeRecipe> ResolvedRecipeSchemaFunction wrapFunc(Consumer<T> function) {
		return (cx, args) -> function.accept((T) cx.recipe());
	}

	public static <T extends KubeRecipe, A1> ResolvedRecipeSchemaFunction wrapFunc(RecipeComponent<A1> param1, BiConsumer<T, A1> function) {
		List<RecipeComponent<?>> params = List.of(param1);
		return new ResolvedRecipeSchemaFunction() {

			@Override
			public List<RecipeComponent<?>> arguments() {
				return params;
			}

			@Override
			public void execute(RecipeScriptContext cx, List<Object> args) {
				function.accept((T) cx.recipe(), (A1) args.get(0));
			}
		};
	}

	public static <T extends KubeRecipe, A1, A2> ResolvedRecipeSchemaFunction wrapFunc(RecipeComponent<A1> param1, RecipeComponent<A2> param2, TriConsumer<T, A1, A2> function) {
		List<RecipeComponent<?>> params = List.of(param1, param2);
		return new ResolvedRecipeSchemaFunction() {

			@Override
			public List<RecipeComponent<?>> arguments() {
				return params;
			}

			@Override
			public void execute(RecipeScriptContext cx, List<Object> args) {
				function.accept((T) cx.recipe(), (A1) args.get(0), (A2) args.get(1));
			}
		};
	}

	public static <T extends KubeRecipe, A1, A2, A3> ResolvedRecipeSchemaFunction wrapFunc(RecipeComponent<A1> param1, RecipeComponent<A2> param2, RecipeComponent<A3> param3, QuadConsumer<T, A1, A2, A3> function) {
		List<RecipeComponent<?>> params = List.of(param1, param2, param3);
		return new ResolvedRecipeSchemaFunction() {

			@Override
			public List<RecipeComponent<?>> arguments() {
				return params;
			}

			@Override
			public void execute(RecipeScriptContext cx, List<Object> args) {
				function.accept((T) cx.recipe(), (A1) args.get(0), (A2) args.get(1), (A3) args.get(2));
			}
		};
	}

	interface QuadConsumer<I, A1, A2, A3> {
		void accept(final I inst, final A1 p1, final A2 p2, final A3 p3);
	}
}
