package stepDefinitions;

import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import helpers.MailExtractor;
import helpers.Procedures;
import org.junit.Assert;

public class GeneralStepsDefs extends stepDefinitions.BaseStepsDefs {

    private static int currentPosition  = 0;
    private static String eSlipName     = "";
    private static String userId        = "";
    private static final String appUrl  = "http://cssitcacweb01-dev.azurewebsites.net";

    @Given("^Setup browser$")
    public void setupBrowser() {
        initDriver();
        initPages();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
    }

    @Given("^Generate unique name$")
    public void generateName() throws Exception {
        eSlipName   = Procedures.generateRandomizedStringWithLength(10);
        userId      = Procedures.generateRandomizedStringWithLength(8);
    }

    @Given("^Close browser$")
    public void closeBrowser() {
        driver.quit();
    }

    @Given("^User is on login page$")
    public void userIsOnLoginPage() {
        driver.get(appUrl);
    }

    @When("^User logs out$")
    public void userLogsOut() {
        landingPage.logOut();
    }

    @When("^User enters '(.+)' and '(.+)'$")
    public void userEntersLoginAndPassword(String user, String pass) throws Exception {
        loginPage.login(user, pass);
    }

    @When("^Created user enters '(.+)' and '(.+)'$")
    public void createdUserEntersLoginAndPassword(String user, String pass) throws Exception {
        String password = MailExtractor.getPasswordFromLastEmail();
        loginPage.login(userId.concat("@csiodev.onmicrosoft.com"), password);
    }

    @Then("^Main page is displayed$")
    public void mainPageIsDisplayed()  {
        Assert.assertTrue(landingPage.verifyMainPageIsDisplayed());
    }

    @Given("^User is on dashboard page$")
    public void userIsOnMainPage() {
        landingPage.navigateTo("Dashboard");
    }

    @When("^User creates new user with given data$")
    public void userCreatesNewUserWithGivenData(DataTable table) throws Exception {
        landingPage.navigateTo("Create User");
        createUser.createNewUser(table, userId);
    }

    @Then("^User '(.+)' is created$")
    public void userIsCreated(String user) {
        landingPage.navigateTo("User List");
        Assert.assertTrue(listOfUsers.userVisibleOnUserList(user));
    }

    @Then("^Proper error message '(.+)' is displayed$")
    public void verifyErrorMessage(String message) {
        Assert.assertTrue(createSingleESlip.verifyWarningMessage(message));
    }

    @Given("^User is on user listing page$")
    public void userIsOnUserListingPage() {
        landingPage.navigateTo("User List");
    }

    @When("^User activates selected '(.+)'$")
    public void userActivatesSelectedUser(String user)  {
        listOfUsers.selectUserByName(user);
        listOfUsers.activateUser();
    }

    @Then("^'(.+)' user is activated$")
    public void userIsActivated(String user)  {
        Assert.assertEquals("true", listOfUsers.getUserActivationStatus(user));
    }

    @When("^User deactivates selected '(.+)'$")
    public void userDeactivatesSelectedUser(String user){
        listOfUsers.selectUserByName(user);
        listOfUsers.deactivateUser();
    }

    @Then("^'(.+)' user is deactivated$")
    public void userIsDeactivated(String user)  {
        Assert.assertEquals("false", listOfUsers.getUserActivationStatus(user));
    }

    @When("^User clicks next$")
    public void userClicksNext() {
        createSingleESlip.next();
    }

    @Given("^User is creating new eEslip$")
    public void openNewESlipPage() {
        landingPage.navigateTo("Create Single");
    }

    @When("^User creates new eSlip with given customer and policy information with given data$")
    public void fillCustomerInformation(DataTable table) {
        landingPage.navigateTo("Create Single");
        createSingleESlip.fillCustomerInformation(eSlipName, table);
    }

    @When("^User saves eSlip draft$")
    public void userSavesESlipDraft() {
        createSingleESlip.saveDraft();
    }

    @Given("^User opens drafted '(.+)' eSlip$")
    public void userOpensDraftedNameESlip(String name) {
        landingPage.navigateTo("Drafts");
        listOfDrafts.selectESlipByName(eSlipName);
        listOfDrafts.editESlip();
    }

    @Then("^'(.+)' page is displayed$")
    public void verifyPageTitle(String title){
        createSingleESlip.verifyPageTitle(title);
    }

    @When("^User adds new vehicle with given data$")
    public void userAddsNewVehicleWithGivenData(DataTable table) {
        createSingleESlip.fillVehicleInformation(table);
    }

    @When("^User adds new back text section with given data$")
    public void userAddsNewBackTextSectionWithGivenData(DataTable table)  {
        createSingleESlip.fillBackText(table);
        createSingleESlip.addBackTextEntry();
    }

    @Then("^Vehicle with '(.+)' is added to eSlip$")
    public void verifyNoOfVehiclesOnTheList(String vin) {
        Assert.assertTrue(createSingleESlip.verifyIfVehicleWithVinIsListed(vin));
    }

    @Then("^Back text with '(.+)' is added to eSlip$")
    public void verifyNoOfBackTextsOnTheList(String title) {

    }

    @Then("^ESlip '(.+)' is displayed on Drafts list$")
    public void eslipNameIsDisplayedOnDraftsList(String name) {
        landingPage.navigateTo("Drafts");
        Assert.assertTrue(listOfDrafts.verifyIfESlipInDisplayedOnList(eSlipName));
    }

    @When("^User moves up vehicle with '(.+)' vin number$")
    public void userMovesUpVehicleWithVinVinNumber(String vin) {
        currentPosition = createSingleESlip.getVehicleByVinPosition(vin);
        createSingleESlip.moveVehicleUp(vin);
    }

    @When("^User moves down vehicle with '(.+)' vin number$")
    public void userMovesDownVehicleWithVinVinNumber(String vin) {
        currentPosition = createSingleESlip.getVehicleByVinPosition(vin);
        createSingleESlip.moveVehicleDown(vin);
    }

    @Then("^Vehicle with '(.+)' is reordered$")
    public void vehicleOrderIsChanged(String vin) {
        Assert.assertNotEquals(currentPosition, createSingleESlip.getVehicleByVinPosition(vin));
    }

    @When("^User removes vehicle with '(.+)' vin number$")
    public void userRemovesVehicleWithTemp(String vin)  {
        createSingleESlip.removeVehicle(vin);
    }

    @Then("^Vehicle with '(.+)' vin number is removed from eSlip$")
    public void vehicleIsRemovedFromESlip(String vin) {
        Assert.assertFalse(createSingleESlip.verifyIfVehicleWithVinIsListed(vin));
    }

    @When("^User moves up back text with '(.+)' title$")
    public void userMovesUpBackTextWithTempTitle(String title) {
        currentPosition = createSingleESlip.getBackTextByTitlePosition(title);
        createSingleESlip.moveBackTextUp(title);
    }

    @When("^User moves down back text with '(.+)' title$")
    public void userMovesDownBackTextWithTempTitle(String title) {
        currentPosition = createSingleESlip.getBackTextByTitlePosition(title);
        createSingleESlip.moveBackTextDown(title);
    }

    @Then("^Back text with '(.+)' title is reordered$")
    public void backTextWithTempTitleIsReordered(String title) {
        Assert.assertNotEquals(currentPosition, createSingleESlip.getBackTextByTitlePosition(title));
    }

    @When("^User removes back text with '(.+)' title$")
    public void userRemovesBackTextWithTempTitle(String title) {
        createSingleESlip.removeBackText(title);
    }

    @Then("^Back text with '(.+)' is removed from eSlip$")
    public void backTextWithTempIsRemovedFromESlip(String title) {
        Assert.assertFalse(createSingleESlip.verifyIfBackTextWithTitleIsListed(title));
    }

    @When("^User fill email form with given data$")
    public void userFillEmailFormWith(DataTable table) {
        createSingleESlip.fillSendPreview(table);
    }

    @When("^User sends created eSlip$")
    public void userSendsCreatedESlip() {
        createSingleESlip.sendESlip();
    }

    @Then("^Created '(.+)' eSlip is sent to user$")
    public void createdNameESlipIsSentToUser(String name) {
        landingPage.navigateTo("Sent");
        Assert.assertTrue(sentESlips.verifyIfESlipInDisplayedOnList(eSlipName));
    }

    @Then("^User has access to '(.+)' modules$")
    public void userHasAccessToModulesAvailableModules(String listOfModules) {
        Assert.assertTrue(landingPage.verifyAvailableModules(listOfModules));
    }

    @Then("^User does not have access to '(.+)' modules$")
    public void userDoesNotHaveAccessToModulesUnavailableModules(String listOfModules) {
        Assert.assertTrue(landingPage.verifyUnavailableModules(listOfModules));
    }

    @Given("^Clear email account$")
    public void clearEmailAccount() throws Exception {
        MailExtractor.deleteMessages();
        Assert.assertEquals(0, MailExtractor.getNoOfMessages());
    }

    @Then("^ESlips are sent to given email account$")
    public void eslipsAreSentToGivenEmailAccount() throws Exception {
        Assert.assertEquals("eSlip", MailExtractor.getLastEmailTitle());
    }
}
