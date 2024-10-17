package seedu.internbuddy.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.internbuddy.commons.core.index.Index;
import seedu.internbuddy.logic.Messages;
import seedu.internbuddy.logic.commands.exceptions.CommandException;
import seedu.internbuddy.model.Model;
import seedu.internbuddy.model.application.Application;
import seedu.internbuddy.model.company.Company;
import seedu.internbuddy.model.company.Status;

/**
 * Withdraws an application identified using it's displayed index from the address book.
 */
public class WithdrawCommand extends Command {

    public static final String COMMAND_WORD = "withdraw";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Withdraws the application identified by the index number used in the displayed application list.\n"
            + "Parameters: COMPANYINDEX (must be a positive integer) "
            + "APPLICATIONINDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 1";

    public static final String MESSAGE_WITHDRAW_APPLICATION_SUCCESS = "Withdrawn application: %1$s";

    private final Index companyIndex;
    private final Index applicationIndex;

    /**
     * @param companyIndex of the company in the filtered company list to withdraw application from
     * @param applicationIndex of the application in the company to withdraw
     */
    public WithdrawCommand(Index companyIndex, Index applicationIndex) {
        this.companyIndex = companyIndex;
        this.applicationIndex = applicationIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Company> lastShownList = model.getFilteredCompanyList();

        if (companyIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
        }

        Company companyToEdit = lastShownList.get(companyIndex.getZeroBased());
        List<Application> applicationList = new ArrayList<>(companyToEdit.getApplications());

        if (applicationIndex.getZeroBased() >= applicationList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_APPLICATION_DISPLAYED_INDEX);
        }

        Application applicationToWithdraw = applicationList.get(applicationIndex.getZeroBased());
        applicationList.remove(applicationToWithdraw);

        Status newStatus = getNewStatus(companyToEdit, applicationList);

        Company editedCompany = new Company(companyToEdit.getName(), companyToEdit.getPhone(), companyToEdit.getEmail(),
                companyToEdit.getAddress(), companyToEdit.getTags(), newStatus, applicationList);
        model.setCompany(companyToEdit, editedCompany);
        return new CommandResult(String.format(MESSAGE_WITHDRAW_APPLICATION_SUCCESS, applicationToWithdraw));
    }

    public static Status getNewStatus(Company companyToEdit, List<Application> applicationList) {
        if (applicationList.isEmpty()) {
            return new Status("CLOSED");
        } else {
            return companyToEdit.getStatus();
        }
    }
}