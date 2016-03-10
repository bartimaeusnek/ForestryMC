/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nonnull;

import forestry.api.genetics.IAlleleProperty;

/**
 * Simple allele encapsulating an {@link IFruitProvider}.
 */
public interface IAlleleFruit extends IAlleleProperty<IAlleleFruit> {

	@Nonnull
	IFruitProvider getProvider();

	@Nonnull
	String getModelName();

	@Nonnull
	String getModID();

}
