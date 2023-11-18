
CFLAGS=-Wall -Wextra -pedantic

.PHONY: clean check distcheck

hello:

clean:
	$(RM) hello *~

check:
	exit 0

distcheck:
	exit 0

