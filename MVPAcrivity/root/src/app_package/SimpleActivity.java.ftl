package ${packageName};

import net.ed58.dlm.clients.base.BaseCoreMVPActivity;
import ${packageName}.${presentName};
import ${superClassFqcn};
import android.os.Bundle;


public class ${activityClass} extends BaseCoreMVPActivity<${presentName},${presentName}.Ui> implements ${presentName}.Ui  {

	@Override
    public int getLayoutId() {
        return R.layout.${layoutName};
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected ${presentName} createPresenter() {
        return null;
    }

    @Override
    protected ${presentName}.Ui getUi() {
        return this;
    }
}
