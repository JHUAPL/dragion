.. dragion documentation master file, created by
   sphinx-quickstart on Thu Aug 25 16:19:38 2022.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

dragion
=======

A Java API for modeling electrostatic interactions between atmospheric ions and charged surfaces, including:

- arbitrarily shaped homogeneous dielectrics
- biased and floating conductors
- self-consistent surface charging through ion collection
- ion diffusion
- ion mobility (in the presence of external and surface fields)
- volumetric ion sources
- collisional ion loss

----

Getting started
===============


Get the code from <http://hardin.jhuapl.edu:8080/scm/zimmemi1/dragion>. Assume that it is cloned into `~/dragion`.

You will need to compile [VTK 8.2](vtk.org/download) from source, with Java bindings enabled. Typically on MacOS or Linux, using [CMake](https://cmake.org/), this looks like::

	wget https://www.vtk.org/files/release/8.2/VTK-8.2.0.tar.gz
	tar xf VTK-8.2.0.tar.gz
	cd VTK-8.2.0
	mkdir build
	cd build
	cmake -DCMAKE_INSTALL_PREFIX=~/local/vtk-8.2 -DVTK_WRAP_JAVA=TRUE ..
	make install

This installs vtk.jar and a multitude of dynamically linked VTK sub-libraries into `~/local/vtk-8.2/lib`.

For now the classpath and runtime library path are passed explicitly to the JRE from the command line (this will probably change in a future release, i.e. by packing all required libs and natives into a single runnable jar).

To print a helpful message, try::

	DYLD_LIBRARY_PATH=~/local/vtk-8.2/lib \
	java -Djava.library.path=/Users/zimmemi1/local/vtk-8.2/lib \
	-classpath ~/dragion/bin:~/dragion/lib/commons-math3-3.6.1/commons-math3-3.6.1.jar:~/dragion/lib/guava-28.1/guava-28.1-jre.jar:~/local/vtk-8.2/lib/vtk.jar:~/dragion/lib/tomlj-1.0.0/tomlj-1.0.0.jar:~/dragion/lib/commons-cli-1.5.0/commons-cli-1.5.0.jar:~/dragion/lib/antlr4-runtime-4.7.2/antlr4-runtime-4.7.2.jar \
	dragion.Main 


.. toctree::
   :maxdepth: 2
   :caption: Contents:



Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`
