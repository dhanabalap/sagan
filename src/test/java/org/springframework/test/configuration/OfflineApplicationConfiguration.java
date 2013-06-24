package org.springframework.test.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.guides.GettingStartedGuide;
import org.springframework.site.guides.GettingStartedService;
import org.springframework.site.guides.GuideRepo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Import(ApplicationConfiguration.class)
public class OfflineApplicationConfiguration{

	@Bean
	public BeanPostProcessor offlineGettingStartedService() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof GettingStartedService) {
					return new OfflineGettingStartedService();
				}
				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				return bean;
			}
		};
	}

	private class OfflineGettingStartedService implements GettingStartedService {
		@Override
		public GettingStartedGuide loadGuide(String guideId) {
			return new GettingStartedGuide("Awesome getting started guide that isn't helpful");
		}

		@Override
		public List<GuideRepo> listGuides() {
			ObjectMapper mapper = new ObjectMapper();
			try {
				String reposJson = "/org/springframework/site/guides/springframework-meta.repos.offline.json";
				InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
				return mapper.readValue(json, new TypeReference<List<GuideRepo>>(){});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public byte[] loadImage(String guideSlug, String imageName) {
			return new byte[0];
		}
	}
}