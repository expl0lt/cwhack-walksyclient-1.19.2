package net.walksy.client.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.walksy.client.WalksyClient;
import org.apache.http.util.TextUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PlayerUtils {
    private static final Color color = new Color();


    public static Vec3d getLerpedPos(Entity e, float partialTicks)
    {
        double x = MathHelper.lerp(partialTicks, e.lastRenderX, e.getX());
        double y = MathHelper.lerp(partialTicks, e.lastRenderY, e.getY());
        double z = MathHelper.lerp(partialTicks, e.lastRenderZ, e.getZ());
        return new Vec3d(x, y, z);
    }

    public static List<ColoredText> toColoredTextList(Text text) {
        Stack<ColoredText> stack = new Stack<>();
        List<ColoredText> coloredTexts = new ArrayList<>();
        preOrderTraverse(text, stack, coloredTexts);
        coloredTexts.removeIf(e -> e.getText().equals(""));
        return coloredTexts;
    }
    public static Map<Color, Integer> getColoredCharacterCount(List<ColoredText> coloredTexts) {
        Map<Color, Integer> colorCount = new HashMap<>();

        for (ColoredText coloredText : coloredTexts) {
            if (colorCount.containsKey(coloredText.getColor())) {
                // Since color was already catalogued, simply update the record by adding the length of the new text segment to the old one
                colorCount.put(coloredText.getColor(), colorCount.get(coloredText.getColor()) + coloredText.getText().length());
            } else {
                // Add new entry to the hashmap
                colorCount.put(coloredText.getColor(), coloredText.getText().length());
            }
        }

        return colorCount;
    }
    public static Color getMostPopularColor(Text text) {
        Comparator<Integer> integerComparator = Comparator.naturalOrder();
        Optional<Map.Entry<Color, Integer>> optionalColor = getColoredCharacterCount(toColoredTextList(text))
                .entrySet().stream()
                .max((a, b) -> integerComparator.compare(a.getValue(), b.getValue()));

        return optionalColor.map(Map.Entry::getKey).orElse(new Color(255, 255, 255));
    }

    private static void preOrderTraverse(Text text, Stack<ColoredText> stack, List<ColoredText> coloredTexts) {
        if (text == null)
            return;

        // Do actions here
        String textString = text.getString();

        TextColor mcTextColor = text.getStyle().getColor();


        // If mcTextColor is null, the color should be inherited from its parent. In this case, the path of the recursion is stored on the stack,
        // with the current element's parent at the top, so simply peek it if possible. If not, there is no parent element,
        // and with no color, use the default of white.
        Color textColor;
        if (mcTextColor == null) {
            if (stack.empty())
                // No color defined, use default white
                textColor = new Color(255, 255, 255);
            else
                // Use parent color
                textColor = stack.peek().getColor();
        } else {
            // Has a color defined, so use that
            textColor = new Color((text.getStyle().getColor().getRgb()) | 0xFF000000); // Sets alpha to max. Some damn reason Color's packed ctor is in ARGB format, not RGBA
        }

        ColoredText coloredText = new ColoredText(textString, textColor);
        coloredTexts.add(coloredText);
        stack.push(coloredText); // For the recursion algorithm's child, the current coloredText is its parent, so add to stack
        // Recursively traverse
        for (Text child : text.getSiblings())
            preOrderTraverse(child, stack, coloredTexts);

        stack.pop();
    }

    public static Dimension getDimension() {
        if (WalksyClient.getClient().world == null) return Dimension.Overworld;

        return switch (WalksyClient.getClient().world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };

    }
    public static double squaredDistanceToCamera(double x, double y, double z) {
        return WalksyClient.getClient().gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
    }
    public static boolean isWithinCamera(Entity entity, double r) {
        return squaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ()) <= r * r;
    }

    public static double distanceToCamera(double x, double y, double z) {
        return Math.sqrt(squaredDistanceToCamera(x, y, z));
    }

    public static double distanceToCamera(Entity entity) {
        return distanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

}
