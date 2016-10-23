package com.example.ankit.myapplication;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhysicsEngine {
    private List<Sprite> _sprites;
    public PhysicsEngine(Sprite... spriteArray) {
        if(spriteArray != null && spriteArray.length > 0) {
            _sprites = Arrays.asList(spriteArray);
            return;
        }
        _sprites = new ArrayList<>();
    }
    public void addSprites(Sprite... sprites) {
        _sprites.addAll(Arrays.asList(sprites));
    }
    public void update() {
        for(Sprite sprite:_sprites) {
            sprite.incrementXVelocity(sprite.getXAcceleration());
            sprite.incrementYVelocity(sprite.getYAcceleration());
            sprite.incrementXCoords(sprite.getXVelocity());
            sprite.incrementYCoords(sprite.getYVelocity());
        }
    }

}
