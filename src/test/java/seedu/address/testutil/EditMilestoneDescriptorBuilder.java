package seedu.address.testutil;

import seedu.address.logic.commands.EditProjectMilestoneCommand;
import seedu.address.model.project.Description;
import seedu.address.model.project.Milestone;
import seedu.address.model.util.PocketProjectDate;

/**
 * A utility class to help with building EditMilestoneDescriptor objects.
 */
public class EditMilestoneDescriptorBuilder {

    private EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor;

    public EditMilestoneDescriptorBuilder() {
        descriptor = new EditProjectMilestoneCommand.EditMilestoneDescriptor();
    }

    public EditMilestoneDescriptorBuilder(EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor) {
        this.descriptor = new EditProjectMilestoneCommand.EditMilestoneDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditMilestoneDescriptor} with fields containing {@code milestone}'s details
     */
    public EditMilestoneDescriptorBuilder(Milestone milestone) {
        descriptor = new EditProjectMilestoneCommand.EditMilestoneDescriptor();
        descriptor.setMilestoneDesc(milestone.getMilestoneDescription());
        descriptor.setDate(milestone.getDate());
    }

    /**
     * Sets the {@code milestoneDesc} of the {@code EditMilestoneDescriptor} that we are building.
     */
    public EditMilestoneDescriptorBuilder withMilestoneDesc(String desc) {
        descriptor.setMilestoneDesc(new Description(desc));
        return this;
    }

    /**
     * Sets the {@code date} of the {@code EditMilestoneDescriptor} that we are building.
     */
    public EditMilestoneDescriptorBuilder withDate(String date) {
        descriptor.setDate(new PocketProjectDate(date));
        return this;
    }

    public EditProjectMilestoneCommand.EditMilestoneDescriptor build() {
        return descriptor;
    }
}

