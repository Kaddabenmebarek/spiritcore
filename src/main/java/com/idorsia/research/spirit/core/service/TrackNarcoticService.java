package com.idorsia.research.spirit.core.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

@Service
public class TrackNarcoticService implements Serializable {

	private static final long serialVersionUID = -7471336746536971086L;

	/*private LithiumService lithiumService = LithiumServiceFactory.getLithiumService();

	public Entity<?> registerLithiumUsage(String barcode, String amountString, String username, String elb,
			boolean discardSample) {
		Entity<?> entity = null;
		boolean isEntity = false;
		if (barcode.startsWith("VTS-")) {
			entity = lithiumService.findSample(barcode);
			isEntity = true;
		} else if (barcode.startsWith("VTM-")) {
			entity = lithiumService.findMixture(barcode);
			isEntity = true;
		}
		if (entity != null) {
			try {
				if(elb==null || elb.trim().equals(""))
					throw new InvalidDataException("Please enter an ELB");
				Float amount = Float.parseFloat(amountString);
				if (entity instanceof Mixture) {
					if (((Mixture) entity).getDiscarded())
						throw new InvalidDataException("Mixture is discarded");
					entity = lithiumService.registerUsage((Mixture) entity, amount, "ml", elb,
							Calendar.getInstance().getTime(), username);
				} else if (entity instanceof Sample) {
					if (((Sample) entity).getDiscarded())
						throw new InvalidDataException("Sample is discarded");
					entity = lithiumService.registerUsage((Sample) entity, amount, "ml", elb,
							Calendar.getInstance().getTime(), username);
				} else {
					throw new InvalidDataException("You can only register a usage from a Sample or a Mixture");
				}
				if (discardSample)
					lithiumService.discardEntity(entity);
			} catch (NumberFormatException ex) {
				throw new NumberFormatException("You should enter a valid amount.");
			}
		} else if (isEntity) {
			throw new InvalidDataException("Sample or Mixture not found");
		}
		return entity;
	}

	public Sample findSample(String barcode) {
		return lithiumService.findSample(barcode.toUpperCase());
	}

	public Mixture findMixture(String barcode) {
		return lithiumService.findMixture(barcode.toUpperCase());
	}

	public Float getAmount(Entity<?> entity) {
		if (entity != null) {
			Float conversion = 1f;
			if (entity instanceof Sample) {
				Sample sample = (Sample) entity;
				if (sample.getLeftAmountUnit().toUpperCase().equals("UL"))
					conversion = 0.001f;
				else if (sample.getLeftAmountUnit().toUpperCase().equals("L"))
					conversion = 1000f;
				return sample.getLeftAmount() * conversion;
			} else if (entity instanceof Mixture) {
				Mixture mixture = (Mixture) entity;
				if (mixture.getUnit().toUpperCase().equals("UL"))
					conversion = 0.001f;
				else if (mixture.getUnit().toUpperCase().equals("L"))
					conversion = 1000f;
				return mixture.getLeftAmount() * conversion;
			}
		}
		return null;
	}

	public String generateSampleBarcode() {
		return lithiumService.generateBarcode("VTS", "SAMPLE");
	}

	public void createSample(Sample newSample, Sample parentSample, boolean discardSampleParent) {
		if (newSample.getElb() == null || newSample.getElb().trim().equals("")) {
			throw new InvalidDataException("Cannot register bottle, no ELB attached to this study !");
		} else if (!lithiumService.isAnalgesia(parentSample)
				&& !lithiumService.isAnesthesia(parentSample)) {
			throw new InvalidDataException(
					"You can only create sample from an analgesia or an anesthesia substance!");
		} else if (!parentSample.getLeftAmountUnit().toUpperCase().contains("L")) {
			throw new InvalidDataException("You can only create sample from a solution !");
		} else {
			lithiumService.registerSampleFromParentSample(parentSample, newSample,discardSampleParent);
		}
	}

	public boolean isAnalgesia(Sample newSample) {
		return lithiumService.isAnalgesia(newSample);
	}

	public Mixture createMixture() {
		return lithiumService.createMixture();
	}

	public void addMixtureItem(Mixture mixture, Sample sample, Float amount) {
		lithiumService.addMixtureItem(mixture, sample, amount);
	}

	public Mixture registerMixture(Mixture mixture) {
		return lithiumService.registerMixture(mixture);
	}

	public void registerSample(Sample sample) {
		lithiumService.registerSample(sample);
	}

	public void printBarcode(String barcode) {
		lithiumService.print(barcode);		
	}*/
}
