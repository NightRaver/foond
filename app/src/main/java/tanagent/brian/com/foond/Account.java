package tanagent.brian.com.foond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Brian on 1/29/2016.
 */
public class Account extends Activity {

    private Button signUpButton;
    private Button logInButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account);

        signUpButton = (Button) findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, SignUp.class);
                startActivity(intent);
            }
        });

        logInButton = (Button) findViewById(R.id.log_in);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Account.this, "Logging In", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
