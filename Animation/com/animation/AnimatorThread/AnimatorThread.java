package com.animation.AnimatorThread;

import com.animation.Animator.Animator;

public class AnimatorThread extends Thread {
	
	@Override
	public void run() {
		Animator.INSTANCE.animate();
	}
}
