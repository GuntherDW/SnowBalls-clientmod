import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<yq> ingredients;
    private yq result;

    public SnowBallRecipe(yq result, List<yq> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<yq> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<yq> ingredients) {
        this.ingredients = ingredients;
    }

    public yq getResult() {
        return result;
    }

    public void setResult(yq result) {
        this.result = result;
    }

    public void addIngredient(yq ingredient) {
        if (ingredient != null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
