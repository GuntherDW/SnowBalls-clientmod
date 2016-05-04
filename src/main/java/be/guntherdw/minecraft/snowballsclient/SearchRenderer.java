package be.guntherdw.minecraft.snowballsclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author guntherdw
 */
public class SearchRenderer {

    private LiteModSnowBalls modInstance;
    private Set<BlockPos> foundPositions;
    private Set<BlockModel> models;
    private boolean enabled;

    public SearchRenderer(LiteModSnowBalls instance) {
        this.modInstance = instance;
        this.enabled = true;
        foundPositions = new HashSet<BlockPos>();
        models = new HashSet<BlockModel>();
    }

    public void decodePayload(byte[] data) {
        this.clearEntities();
        StringBuilder sb = new StringBuilder();
        for(byte b1 : data) {
            if(b1 == 0) {
                String line = sb.toString();
                String[] coords = line.split(",");

                BlockPos blockPos = new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                foundPositions.add(blockPos);
                models.add(new BlockModel(blockPos));
                sb = new StringBuilder();
            } else {
                sb.append((char)b1);
            }
        }
    }

    private double getPlayerX(float partialTicks) {
        Entity p = Minecraft.getMinecraft().getRenderViewEntity();
        return p.prevPosX + (p.posX - p.prevPosX) * partialTicks;
    }

    private double getPlayerY(float partialTicks) {
        Entity p = Minecraft.getMinecraft().getRenderViewEntity();
        return p.prevPosY + (p.posY - p.prevPosY) * partialTicks;
    }

    private double getPlayerZ(float partialTicks) {
        Entity p = Minecraft.getMinecraft().getRenderViewEntity();
        return p.prevPosZ + (p.posZ - p.prevPosZ) * partialTicks;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void clearEntities() {
        foundPositions.clear();
        models.clear();
    }

    public void onPostRenderEntities(float partialTicks) {
        if(this.enabled && foundPositions.size() > 0) {
            RenderHelper.disableStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();

            boolean foggy = GL11.glIsEnabled(GL11.GL_FOG);
            GlStateManager.disableFog();

            GlStateManager.pushMatrix();

            GlStateManager.translate(
                -getPlayerX(partialTicks),
                -getPlayerY(partialTicks),
                -getPlayerZ(partialTicks));

            /* Draw blocks */

            for(BlockModel model : models) {
                model.render();
            }

            /* End draw blocks */

            GlStateManager.popMatrix();

            if (foggy) {
                GlStateManager.enableFog();
            }
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            RenderHelper.enableStandardItemLighting();
        }
    }

    public class BlockModel {


        private double x, y, z;
        private float lineWidth;
        private int red, green, blue, alpha;

        public BlockModel(BlockPos blockPos) {
            this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        // bounds: [0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375]

        public BlockModel(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;

            lineWidth = 3.0f;
            red = 85;
            green = 228;
            blue = 255;
            alpha = 100;
        }



        public void render() {

            double startingPointX = x;
            double startingPointY = y;
            double startingPointZ = z;
            
            double width = 1.0D;
            double height = 1.0D;
            double girth = 1.0D;

            Tessellator tess = Tessellator.getInstance();
            VertexBuffer wr = tess.getBuffer();

            /*
            wr.startDrawing(GL11.GL_LINES);
            wr.setColorRGBA(red, green, blue, alpha);
            GL11.glLineWidth(lineWidth);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ);
            wr.addVertex(startingPointX,         startingPointY,          startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ + girth);
            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ + girth);

            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ);

            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ + girth);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ + girth);
            wr.addVertex(startingPointX,         startingPointY,          startingPointZ + girth);

            wr.addVertex(startingPointX,         startingPointY,          startingPointZ);
            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ);


            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ + girth);


            wr.addVertex(startingPointX + width, startingPointY,          startingPointZ + girth);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ + girth);


            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ);
            wr.addVertex(startingPointX + width, startingPointY + height, startingPointZ);

            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ);
            wr.addVertex(startingPointX,         startingPointY + height, startingPointZ + girth);
            tess.draw(); */
        }
    }

}
