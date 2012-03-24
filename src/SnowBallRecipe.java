/*
 * Copyright (c) 2012 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<aan> ingredients;
    private aan result;

    public SnowBallRecipe(aan result, List<aan> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<aan> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<aan> ingredients) {
        this.ingredients = ingredients;
    }

    public aan getResult() {
        return result;
    }

    public void setResult(aan result) {
        this.result = result;
    }

    public void addIngredient(aan ingredient) {
        if (ingredient != null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
