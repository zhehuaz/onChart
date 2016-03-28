/*
 *     Copyright 2016 Zhehua Chang
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package zchang.me.uilibrary;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/3/28.
 */
public class CircleBackgroundDrawable extends Drawable {
    private RectF circleBounds;
    private Rect bounds;

    Paint paint;

    public CircleBackgroundDrawable(int color) {
        super();
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        this.bounds = bounds;
        this.circleBounds = new RectF(bounds);
        this.circleBounds.set(this.circleBounds.left * 0.75f, this.circleBounds.top * 0.75f, this.circleBounds.right * 0.75f, this.circleBounds.bottom * 0.75f);
        super.onBoundsChange(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        canvas.translate((bounds.bottom - bounds.top) * 0.125f, (bounds.right - bounds.left) * 0.125f);
        canvas.drawOval(circleBounds, paint);

        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0xcd;
    }
}
