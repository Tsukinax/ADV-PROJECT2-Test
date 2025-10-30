package se233.contra.view;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class Animation {
    private final List<Image> frames;
    private final double frameDuration;
    private double elapsedTime;
    private int currentFrame;
    private boolean loop;
    private boolean finished;

    public Animation(List<Image> frames, double frameDuration) {
        this(frames, frameDuration, true);
    }

    public Animation(List<Image> frames, double frameDuration, boolean loop) {
        this.frames = new ArrayList<>(frames);
        this.frameDuration = frameDuration;
        this.loop = loop;
        this.elapsedTime = 0;
        this.currentFrame = 0;
        this.finished = false;
    }

    public void update(double deltaTime) {
        if (finished && !loop) return;

        elapsedTime += deltaTime;

        if (elapsedTime >= frameDuration) {
            elapsedTime -= frameDuration;
            currentFrame++;

            if (currentFrame >= frames.size()) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.size() - 1;
                    finished = true;
                }
            }
        }
    }

    public Image getCurrentFrame() {
        if (frames.isEmpty()) return null;
        return frames.get(currentFrame);
    }

    public void reset() {
        currentFrame = 0;
        elapsedTime = 0;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getFrameCount() {
        return frames.size();
    }
}