package com.example.demo.domain.common;

public interface DomainConverter<A, B> {
	B convert(A a);
}