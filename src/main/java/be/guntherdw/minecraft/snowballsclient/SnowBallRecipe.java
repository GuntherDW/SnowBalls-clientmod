/*
 * Copyright (c) 2012-2015 GuntherDW
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
package be.guntherdw.minecraft.snowballsclient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<ItemStack> ingredients;
    private ItemStack result;

    public SnowBallRecipe(ItemStack result, List<ItemStack> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void addIngredient(ItemStack ingredient) {
        if (ingredient != null && Item.getIdFromItem(ingredient.getItem()) > 0 && Item.getIdFromItem(ingredient.getItem()) < 1024)
            this.ingredients.add(ingredient);
    }
}
