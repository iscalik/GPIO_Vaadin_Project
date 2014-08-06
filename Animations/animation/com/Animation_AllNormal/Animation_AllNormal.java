package animation.com.Animation_AllNormal;

public class Animation_AllNormal extends com.animation.Animation.Animation {
	
	public Animation_AllNormal() {
		super ("All (normal)");
	}

	@Override
	public void generatePattern() {		
		mPattern.toggle();
	}
}
