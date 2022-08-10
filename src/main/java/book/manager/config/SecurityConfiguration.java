package book.manager.config;

import book.manager.entity.AuthUser;
import book.manager.mapper.UserMapper;
import book.manager.service.impl.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    UserAuthService service;
    @Resource
    PersistentTokenRepository repository;
    @Resource
    UserMapper mapper;

    @Bean
    public PersistentTokenRepository jdbcRepository(@Autowired DataSource dataSource){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        //repository.setCreateTableOnStartup(true);
        return repository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(service)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        HttpSession session = httpServletRequest.getSession();
        AuthUser user = mapper.getPasswordByUsername(authentication.getName());
        session.setAttribute("user", user);
        if (user.getRole().equals("admin")){
            httpServletResponse.sendRedirect("/Book/page/admin/index");
        } else {
            httpServletResponse.sendRedirect("/Book/page/user/index");
        }
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/static/**", "/page/auth/**", "/api/auth/**").permitAll()
                .antMatchers("/page/user/**", "/api/user/**").hasRole("user")
                .antMatchers("/page/admin/**", "/api/admin/**").hasRole("admin")
                .anyRequest().hasAnyRole("user", "admin")

                .and()

                .formLogin()
                .loginPage("/page/auth/login")
                .loginProcessingUrl("/api/auth/login")
                .successHandler(this::onAuthenticationSuccess)

                .and()

                .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login")

                .and()

                .csrf().disable()

                .rememberMe()
                .rememberMeParameter("remember")
                .tokenValiditySeconds(60 * 60 * 24 * 3)
                .tokenRepository(repository);

    }

}
