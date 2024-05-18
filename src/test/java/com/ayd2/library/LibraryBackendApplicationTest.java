package com.ayd2.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LibraryBackendApplication.class)
class LibraryBackendApplicationTest {

	@Test
	public void contextLoads() {
	}

	@Test
	public void main() {
		LibraryBackendApplication.main(new String[] {});
	}

}
