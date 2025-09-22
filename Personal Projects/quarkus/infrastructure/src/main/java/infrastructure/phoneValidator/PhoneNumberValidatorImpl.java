package infrastructure.phoneValidator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import domain.phoneValidator.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;


public class PhoneNumberValidatorImpl
        implements ConstraintValidator<PhoneNumber, String> {

    private boolean allowNull;
    private final Set<PhoneNumberUtil.PhoneNumberType> allowedTypes = Set
            .of(PhoneNumberUtil.PhoneNumberType.MOBILE, PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE);

    @Override
    public void initialize(PhoneNumber phoneNumber) {

        this.allowNull = phoneNumber.allowNull();
    }

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext context) {
        
        if (value == null) {
            return allowNull;
        }

        try {
            PhoneNumberUtil util = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = util.parse(value, null);
            return util.isValidNumber(phoneNumber) && allowedTypes.contains(util.getNumberType(phoneNumber));
        }
        catch (NumberParseException e) {
            return false;
        }
    }
}
