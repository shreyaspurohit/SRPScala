The usage of SRP library is demonstrated in an simplistic way using Play 2.0 framework. For detailed explanation of SRP protocol please read http://srp.stanford.edu.

The javascript side of the code is present in srp.js and usage example is present at login.scala.html. The class ExampleSRPServer provides an implementation of SRPServer trait. The methods login and register shows example usage of ExampleSRPServer. The code is simple to understand and commented (provided SRP, play 2.0 is understood). The logic is improvized so that client sends K and server validates and send S for client to validate with its S (for identifying the correct server). The logic to logout is not present, but can be added easily by clearing the cookie on the server side and removing the current session from server side. Override the value of N if required, but it done must be done at both client (js) and server (scala) must have the same value.

To run the example, cd into srpExample. Execute command "play run" to start the server. (Play 2.0 must be installed). Register a new username with password. This must be generally a SSL using https. Login using existing username/password - shreyas/shreyas or use a new registered user.

The SRP design from http://srp.stanford.edu/design.html exlains the process as:

SRP is the newest addition to a new class of strong authentication protocols that resist all the well-known passive and active attacks over the network. SRP borrows some elements from other key-exchange and identification protcols and adds some subtle modifications and refinements. The result is a protocol that preserves the strength and efficiency of the EKE family protocols while fixing some of their shortcomings.
The following is a description of SRP-6 and 6a, the latest versions of SRP:

  N    A large safe prime (N = 2q+1, where q is prime)
       All arithmetic is done modulo N.
  g    A generator modulo N
  k    Multiplier parameter (k = H(N, g) in SRP-6a, k = 3 for legacy SRP-6)
  s    User's salt
  I    Username
  p    Cleartext Password
  H()  One-way hash function
  ^    (Modular) Exponentiation
  u    Random scrambling parameter
  a,b  Secret ephemeral values
  A,B  Public ephemeral values
  x    Private key (derived from p and s)
  v    Password verifier
The host stores passwords using the following formula:
  x = H(s, p)               (s is chosen randomly)
  v = g^x                   (computes password verifier)
The host then keeps {I, s, v} in its password database. The authentication protocol itself goes as follows:
User -> Host:  I, A = g^a                  (identifies self, a = random number)
Host -> User:  s, B = kv + g^b             (sends salt, b = random number)

        Both:  u = H(A, B)

        User:  x = H(s, p)                 (user enters password)
        User:  S = (B - kg^x) ^ (a + ux)   (computes session key)
        User:  K = H(S)

        Host:  S = (Av^u) ^ b              (computes session key)
        Host:  K = H(S)
Now the two parties have a shared, strong session key K. To complete authentication, they need to prove to each other that their keys match. One possible way:
User -> Host:  M = H(H(N) xor H(g), H(I), s, A, B, K)
Host -> User:  H(A, M, K)
The two parties also employ the following safeguards:
The user will abort if he receives B == 0 (mod N) or u == 0.
The host will abort if it detects that A == 0 (mod N).
The user must show his proof of K first. If the server detects that the user's proof is incorrect, it must abort without showing its own proof of K.

