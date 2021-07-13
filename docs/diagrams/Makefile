.PHONY: all

all: Actions.pdf Actors.pdf Messages.pdf Sequences.pdf Steps.pdf Top.pdf Assertions.pdf

%.pdf: %.svg
	rsvg-convert -f pdf -o $@ $<
