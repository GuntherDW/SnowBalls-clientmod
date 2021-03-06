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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class SnowBallShapedRecipe {

    private Map<Integer, ItemStack> ingredients;
    private ItemStack result;

    public SnowBallShapedRecipe(ItemStack result) {
        this.result = result;
        this.ingredients = new HashMap<Integer, ItemStack>();
    }

    public SnowBallShapedRecipe(ItemStack result, Map<Integer, ItemStack> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public SnowBallShapedRecipe(ItemStack result, List<ItemStack> ing) {
        this.result = result;
        this.ingredients = new HashMap<Integer, ItemStack>();
        for (int x = 0; x < 9; x++) {
            if (ing.get(x) != null) {
                ingredients.put(x, ing.get(x));
            }
        }
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public Map<Integer, ItemStack> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<Integer, ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public void setIngredientSpot(int spot, ItemStack ingredient) {
        if (spot >= 0 && spot < 9) {
            this.ingredients.put(spot, ingredient);
        }
    }

    public ItemStack getIngredientSpot(int spot) {
        if (this.ingredients.containsKey(spot)) {
            return this.ingredients.get(spot);
        } else {
            return null;
        }
    }

    public Object[] generateRecipeLine() {
        // Character c = 'A';
        List<Object> objects = new ArrayList<Object>();
        String biggestline = "";
        String line = "";
        List<Integer> ings = new ArrayList<Integer>();
        List<Object> inglines = new ArrayList<Object>();
        List<String> lines = new ArrayList<String>();
        Integer rows = 0, cols = 0;

        int ingcount = 0;

        for (ItemStack ing : ingredients.values()) {
            if (!ings.contains(Item.getIdFromItem(ing.getItem()))) {
                ings.add(Item.getIdFromItem(ing.getItem()));
                char c = 'A';
                c += ingcount;
                inglines.add(Character.valueOf(c));
                inglines.add(ing);
                ingcount++;
            }
        }
        for (int x = 0; x < 9; x += 3) {
            line = "";
            for (int i = 0; i < 3; i++) {
                if (ingredients.containsKey(i + x)) {
                    int itemID = Item.getIdFromItem(ingredients.get(i + x).getItem());
                    int pos = ings.indexOf(itemID);
                    char chara = 'A';
                    chara += pos;
                    line += chara;
                    // System.out.println("char : "+chara+" voor itemID : "+itemID+"!");
                } else {
                    line += ' ';
                }
            }
            int linelength = line.trim().length();
            if (cols < linelength) {
                cols = linelength;
                biggestline = line;
            }
            boolean add = true;

            if (linelength == 0 && !(x == 3))
                add = false;

            if (add)
                lines.add(line);
        }
        if (cols == 1) {
            for (int col = 0; col < lines.size(); col++) {
                String lin = lines.get(col).trim();
                lines.set(col, lin);
            }
        } else if (cols == 2) {
            boolean trimmode = (biggestline.charAt(2) == ' ');
            for (int col = 0; col < lines.size(); col++) {
                String lin = lines.get(col);
                // System.out.println(col+": "+lin);
                if (trimmode) {
                    lin = lin.substring(0, 2);
                } else {
                    lin = lin.substring(1, 2);
                }
                lines.set(col, lin);
            }
        }
        objects.addAll(lines);
        objects.addAll(inglines);
        /* for(String s : lines) {
            System.out.println("line:"+s.replace(' ', '.'));
        } */
        return objects.toArray();
    }
}
