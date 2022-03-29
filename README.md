#使用自定义注解注意事项
#由于dagger2 提供的viewModel屏幕旋转后对象会变 违反了viewModel设计初衷
#提供 InjectViewModel 解决问题 但是须按照一下规则定义
#因通过反射获取dagger2 提供viewModelFactory的 所以提供的viewModelFactory 方法名按照规范 provide**Factory 例如：provideMineViewModelFactory
#每个模块应该存在工具类 ***DaggerHelp 命名按照规范 例如：VideoDaggerHelp
#新增模块应在ProcessorAdapter添加模块类型
#使用代理类注入 例如：InjectViewModelProxy.inject(this)
