package d3bug.licensing;

public class SimpleRegistration {

	private String name = "";
	private String email = "";
	private String when = "";
	private String regType = "";
	private String featureCodes = "";
	private String special1 = "";
	private String special2 = "";
	private String special3 = "";
	
	private String salt1 = "oiuwer89";
	private String salt2 = "jkla7";
	private String salt3 = "zztsdkjh02op";
	
	private boolean valid = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWhen() {
		return when;
	}
	public void setWhen(String when) {
		this.when = when;
	}
	public String getRegType() {
		return regType;
	}
	public void setRegType(String regType) {
		this.regType = regType;
	}
	public String getFeatureCodes() {
		return featureCodes;
	}
	public void setFeatureCodes(String featureCodes) {
		this.featureCodes = featureCodes;
	}
	
	public String getSpecial1() {
		return special1;
	}
	public void setSpecial1(String special1) {
		this.special1 = special1;
	}
	public String getSpecial2() {
		return special2;
	}
	public void setSpecial2(String special2) {
		this.special2 = special2;
	}
	public String getSpecial3() {
		return special3;
	}
	public void setSpecial3(String special3) {
		this.special3 = special3;
	}
	private void trimStrings() {
		name.trim();
		email.trim();
		when.trim();
		regType.trim();
		featureCodes.trim();
	}

	public void storeLicenseDetails(ILicenseGenerator b) {
		trimStrings();
		b.put("A");  // Version
		b.put(name);
		b.put(salt1);
		b.put(email);
		b.put(salt2);
		b.put(when);
		b.put(regType);
		b.put(salt3);
		b.put(special1);
		b.put(special2);
		b.put(special3);
		b.put(featureCodes);
	}
	
	public void restoreFrom(ILicenseValidator b) {
		try {
			b.readString();  // version
			setName(b.readString());
			b.readString();
			setEmail(b.readString());
			b.readString();
			setWhen(b.readString());
			setRegType(b.readString());
			b.readString();
			setFeatureCodes(b.readString());
			setSpecial1(b.readString());
			setSpecial2(b.readString());
			setSpecial3(b.readString());
			valid = b.verifySignature();
		} catch (Exception e) {
			valid = false;
		}
	}

	public boolean isValid() {
		return valid;
	}
	
}
