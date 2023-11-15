package com.idorsia.research.spirit.core.conf;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ContextShare implements ApplicationContextAware {

	private static ApplicationContext context;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static boolean isStoredBean(String name) {
		try {
			getContext().getBean(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void storeBean(String name, Object entity) {
		DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) ((ConfigurableApplicationContext) getContext())
				.getBeanFactory();
		registry.destroySingleton(name); // destroys the bean object
		registry.registerSingleton(name, entity); // add to singleton beans cache
	}

}
