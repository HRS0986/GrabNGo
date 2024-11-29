package com.apigateway.apigateway.filter;
import com.apigateway.apigateway.exception.AuthException;
import com.apigateway.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if(validator.isSecured.test(exchange.getRequest())){
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new AuthException("Authorization header not present");
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if(authHeader!= null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }
                try {
//                     template.getForObject("http://auth//validate?token=" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                    throw new RuntimeException("Unauthorized access to application");
                }
            }
            return chain.filter(exchange);
        }));
    }

    public static class Config{

    }
}
