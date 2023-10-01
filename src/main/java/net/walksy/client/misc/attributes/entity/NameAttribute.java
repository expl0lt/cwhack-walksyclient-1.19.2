package net.walksy.client.misc.attributes.entity;

import net.walksy.client.misc.attributes.Attribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class NameAttribute extends Attribute {

    public NameAttribute(LivingEntity entity) {
        super(entity);
    }

    @Override
    public Text getText() {
        return this.getEntity().getName();
    }    
}
