package ${packageName};

import android.content.Intent;
import android.os.IBinder;

public class ${className} extends ${presentClassName} {
    public ${className}(Context context) {
        this(context,null);
    }

    public ${className}(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ${className}(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }   
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
