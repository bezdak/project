package sk.tuke.oop.aliens;

import sk.tuke.oop.aliens.actor.AbstractActor;
import sk.tuke.oop.framework.Animation;

public class Hammer extends AbstractActor{

    private static final Animation hammerAnim = new Animation("images/hammer.png", 16, 16, 10);
    private int uses;

    public int getUses() {
        return this.uses;
    }
    public void setUses(int uses) {
        this.uses = uses;
    }

    public Hammer() {
        setUses(1);
        this.setAnimation(hammerAnim);
    }

    public void use() {
        setUses(getUses() - 1);
        if (getUses() == 0) this.getWorld().removeActor(this);
    }

}
