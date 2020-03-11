package com.worldchip.advertising.client.transformer;

import com.worldchip.advertising.client.utils.ABaseTransformer;

import android.view.View;

public class DefaultTransformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
	}

	@Override
	public boolean isPagingEnabled() {
		return true;
	}
}