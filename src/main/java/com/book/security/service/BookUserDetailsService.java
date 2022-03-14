package com.book.security.service;

import java.util.ArrayList;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookUserDetailsService implements UserDetailsService {
    public BookUserDetailsService() {
    }

    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new User("foo", "foo", new ArrayList());
    }
}
