Bipolar ions: uniform source and loss (unfinished)
==================================================

Problem Statement
-----------------

The bipolar balance equation (in the absence of aerosol particles) is

.. math::
	:nowrap:

	\begin{eqnarray}
		\frac{\mathrm{d}n_+}{\mathrm{d}t} = q - \alpha n_+ n_- \\
		\frac{\mathrm{d}n_-}{\mathrm{d}t} = q - \alpha n_+ n_-
	\end{eqnarray}

where :math:`n_\pm` are respective ion concentrations and :math:`\alpha\sim10^{-12} \: \mathrm{m}^3 \: \mathrm{s}^{-1}` [Harrison and Tammet]. Sans aerosols, :math:`n_+=n_-`.

**dragion** can be tested against this bipolar balance equation with uniform ``VolumeSource`` and ``CollisionalSinkBipolar`` instances for each ion species. 

The implementation is forthcoming, to be based on ``dragion/simulations/unipolar/sourcesinktest/SourceSinkTest.java``.

The ``VolumeSource`` objects uniformly distribute positive and negative ions across the simulation ``Box`` at a steady rate, representing the constant source term :math:`q`.

The ``CollisionalSinkBipolar`` algorithm simulates the respective loss terms :math:`- \alpha n_+n_-` by overlaying a uniform grid on the simulation domain, and at each timestep
(1) computing :math:`n_+n_-` in each grid cell
(2) removing :math:`\alpha \: n_+n_- \: \mathrm{d}t \: \mathrm{d}^3r \: / np2c` macroparticles of each species from the grid cell (leaving zero if exceeding existing number)

We would expect the simulated :math:`\frac{\mathrm{d}n}{\mathrm{d}t}` to equal :math:`0`, integrated over many timesteps; in other words, the simulated concentration should be constant and the positive and negative concentrations equal.

Numerical Validation
--------------------

Taking :math:`q = 4\times10^6 \mathrm{m}^{-3} \mathrm{s}^{-1}` and :math:`\frac{\mathrm{d}n_+}{\mathrm{d}t}=0` yields a steady-state concentration of :math:`n_{equil}=\sqrt{\frac{q}{\alpha}}=2\times10^9 \: \mathrm{m}^{-3}` [Lorenz, MMRTG].

TODO



