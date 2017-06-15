package com.dhl.tools.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by liuso on 2017/4/15.
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(8);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.ignoring().antMatchers("/importTemplate/**", "/js/**", "/css/**", "/image/**", "/vendor/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		// 关闭CSRF
		http.csrf().disable().authorizeRequests().antMatchers("/", "/home").permitAll().and().formLogin()
				.loginPage("/login").defaultSuccessUrl("/home").permitAll().and().logout().logoutSuccessUrl("/login")
				.invalidateHttpSession(true);
	}

}
