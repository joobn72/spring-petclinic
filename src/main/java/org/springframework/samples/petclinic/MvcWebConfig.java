package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.web.PetTypeFormatter;
import org.springframework.samples.petclinic.web.VetsAtomView;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.xml.MarshallingView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YongKwon Park
 */
@Configuration
@ComponentScan(basePackages = "org.springframework.samples.petclinic.web")
@EnableWebMvc
public class MvcWebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ClinicService clinicService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(simpleMappingExceptionResolver());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("html", MediaType.TEXT_HTML)
                  .mediaType("xml", MediaType.APPLICATION_XML)
                  .mediaType("atom", MediaType.APPLICATION_ATOM_XML);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(petTypeFormatter());
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver() {
        List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
        viewResolvers.add(beanNameViewResolver());
        viewResolvers.add(internalResourceViewResolver());

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setViewResolvers(viewResolvers);

        return viewResolver;
    }

    @Bean
    public ViewResolver beanNameViewResolver() {
        return new BeanNameViewResolver();
    }

    @Bean
    public ViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        return viewResolver;
    }

    @Bean(name = "vets/vetList.atom")
    public VetsAtomView vetsAtomView() {
        return new VetsAtomView();
    }

    @Bean(name = "vets/vetList.xml")
    public MarshallingView marshallingView() {
        return new MarshallingView(jaxb2Marshaller());
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Vets.class);

        return jaxb2Marshaller;
    }

    @Bean
    public HandlerExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        exceptionResolver.setDefaultErrorView("exception");
        exceptionResolver.setWarnLogCategory("warn");

        return exceptionResolver;
    }

    @Bean
    public PetTypeFormatter petTypeFormatter() {
        return new PetTypeFormatter(clinicService);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");

        return messageSource;
    }

}