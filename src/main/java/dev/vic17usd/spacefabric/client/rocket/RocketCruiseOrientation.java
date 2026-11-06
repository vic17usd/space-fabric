package dev.vic17usd.spacefabric.client.rocket;

import dev.vic17usd.spacefabric.space.RocketJourney;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Visual-only attitude; positions are in the same local frame as the flight scene. */
public final class RocketCruiseOrientation {
    private RocketCruiseOrientation() { }
    public static Vector3f destination(float progress) {
        float p=RocketJourney.smooth(progress);
        return new Vector3f(.8F*(1-p),.4F,12F);
    }
    public static float blend(float progress) {
        float p=Math.clamp(progress,0F,1F);
        return RocketJourney.smooth(p/.22F)*RocketJourney.smooth((1F-p)/.18F);
    }
    /** Turns the rocket's upward axis toward the destination about the rocket's center. */
    public static Quaternionf attitude(float progress,Vector3f direction) {
        if(direction.lengthSquared()<1.0e-8F)return new Quaternionf();
        var target=new Vector3f(direction).normalize();
        return new Quaternionf().slerp(new Quaternionf().rotationTo(0,1,0,target.x,target.y,target.z),blend(progress));
    }
}
