package how.to.finish.the.project.tricevpn.view.ui

import com.dual.pro.one.dualprotocolone.base.BaseActivity
import com.dual.pro.one.dualprotocolone.base.BaseViewModel
import how.to.finish.the.project.tricevpn.R
import how.to.finish.the.project.tricevpn.databinding.ActivityEndBinding

class EndActivity : BaseActivity<BaseViewModel, ActivityEndBinding>() {

    override fun getLayoutRes(): Int = R.layout.activity_end

    override fun getViewModelClass(): Class<BaseViewModel> = BaseViewModel::class.java

    override fun init() {

    }

}