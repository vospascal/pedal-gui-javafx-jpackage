package org.example;

import com.fasterxml.jackson.annotation.JsonInclude;

////	ignore null fields , class level
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class PedalPOJO {
    private int[] throttle;
    private int[] brake;
    private int[] clutch;
    private int[] inverted;

    public int[] getThrottle() {
        return throttle;
    }

    public void setThrottle(int[] throttle) {
        this.throttle = throttle;
    }

    public int[] getBrake() {
        return brake;
    }

    public void setBrake(int[] brake) {
        this.brake = brake;
    }

    public int[] getClutch() {
        return clutch;
    }

    public void setClutch(int[] clutch) {
        this.clutch = clutch;
    }

    public int[] getInverted() {
        return inverted;
    }

    public void setInverted(int[] inverted) {
        this.inverted = inverted;
    }
}
