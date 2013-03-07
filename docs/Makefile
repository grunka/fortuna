all: Fortuna.pdf

clean:
	rm Fortuna.pdf
	rm Fortuna.html

Fortuna.html: Fortuna.md
	markdown Fortuna.md > Fortuna.html

Fortuna.pdf: Fortuna.html
	wkhtmltopdf --footer-center "Page [page] of [toPage]" Fortuna.html Fortuna.pdf

